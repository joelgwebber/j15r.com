# Dead-simple blog server
connect = require 'connect'
fs = require 'fs'
md = (require "node-markdown").Markdown

# Import Views
indexView = require './client/views/index'
postView = require './client/views/post'

_posts = []
_timeline = []

endsWith = (str, frag) ->
  str.substring frag.length == frag

sortTimeline = () ->
  _timeline.sort (a, b) ->
    time = (post) ->
      splat = post.date.split '.'
      100000000 - (splat[0] * 10000) + (splat[1] * 100) + splat[2]
    return (time a) - (time b)

readPosts = (dir) ->
  fs.readdir dir, (err, files) ->
    throw err if err
    count = 0
    total = 0
    for file in files when endsWith file, '.md'
      ++total
      parseMarkdown dir, file, () ->
        ++count
        if count == total
          sortTimeline()

parseMarkdown = (dir, mdFile, cb) ->
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

    if post.date
      # Put the article in the map to be sorted.
      _timeline.push post
    else
      # Set it to empty string so it doesn't render as undefined.
      post.date = ''

    _posts[id] = post
    cb()

getPostHtml = (post) ->
  if !post.html
    post.html = md post.source
  post.html

renderPost = (req, rsp) ->
  name = req.params.article

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

renderIndex = (req, rsp) ->
    data =
      posts: _timeline
    rsp.end (indexView.render data)

module.exports.init = (app) ->
  app.static [ 'blog/static' ]

  app.client 'blog/client/entry.coffee'

  app.get '/blog', renderIndex
  app.get '/blog/', renderIndex
  app.get '/blog/post/:article', renderPost

  readPosts __dirname + '/articles'
