# Dead-simple blog server
connect = require 'connect'
fs = require 'fs'
md = (require "node-markdown").Markdown

# Import Views
indexView = require './client/views/index'
postView = require './client/views/post'

_posts = []

endsWith = (str, frag) ->
  str.substring frag.length == frag

readPosts = (dir) ->
  fs.readdir dir, (err, files) ->
    throw err if err
    parseMarkdown dir, file for file in files when endsWith file, '.md'

parseMarkdown = (dir, mdFile) ->
  fs.readFile dir + '/' + mdFile, 'UTF-8', (err, source) ->
    throw err if err
    post = {}

    while (match = source.match(/^([a-z]+):\s*(.*)\s*\n/i))
      name = match[1]
      name = name[0].toLowerCase() + name.substring 1
      value = match[2]
      source = source.substr match[0].length
      post[name] = value

    id = mdFile.substring 0, mdFile.length - 3 # '.md'
    post.source = source
    post.id = id
    post.title = id if !post.title
    post.date = '' if !post.date

    _posts[id] = post

getPostHtml = (post) ->
  if !post.html
    post.html = md post.source
  post.html

renderPost = (name, rsp) ->
  post = _posts[name]
  if !post
    rsp.status = 404
    rsp.end 'Not found'
    return

  origurl = "http://blog.j15r.com#{post.origurl}" if post.origurl
  rsp.end postView.render
    posts: _posts
    title: post.title
    date: post.date
    content: getPostHtml post
    origurl: origurl

module.exports.init = (app) ->
  app.static [ 'blog/static' ]

  app.client 'blog/client/entry.coffee'

  app.get '/blog/post/:article', (req, rsp, next) ->
    renderPost req.params.article, rsp

  app.get '/blog/:page', (req, rsp) ->
    renderPost req.params.page, rsp

  app.get '/blog', (req, rsp, next) ->
    data =
      posts: _posts
    rsp.end (indexView.render data)

  readPosts __dirname + '/articles'
