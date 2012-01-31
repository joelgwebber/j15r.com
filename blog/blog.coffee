# Dead-simple blog server, using Reed
connect = require 'connect'
reed = require 'reed'
hb = require 'handlebars'
fs = require 'fs'

# Initialize reed
reed.open 'blog/articles'
reed.pages.open 'blog/pages'

# Import Views
makeTemplate = (file) ->
  tmpl = fs.readFileSync file, 'utf8'
  return {
    render: (hb.compile tmpl)
  }
registerPartial = (name, file) ->
  hb.registerPartial name, fs.readFileSync file, 'utf8'

indexView = makeTemplate 'blog/views/index.hb'
postView = makeTemplate 'blog/views/post.hb'
registerPartial 'blog_nav', 'blog/views/nav.hb'

makePosts = (ids) ->
  return ids.map (id) ->
    id: id
    title: id

renderPost = (name, reedGetter, rsp) ->
  posts = null
  content = null
  title = null

  render = () ->
    if !posts || !content
      return

    rsp.end postView.render
      posts: posts
      title: title
      content: content

  reed.list (err, ids) ->
    posts = makePosts ids
    render()

  reedGetter name, (err, meta, html) ->
    content = if err then 'error...' else html
    title = '...title...'
    render()

module.exports.init = (app) ->
  app.static [ 'blog/static' ]

  app.client 'blog/client/entry.coffee'

  app.get '/blog/post/:article', (req, rsp, next) ->
    renderPost req.params.article, reed.get, rsp

  app.get '/blog/:page', (req, rsp) ->
    renderPost req.params.page, reed.pages.get, rsp

  app.get '/blog', (req, rsp, next) ->
    reed.list (err, ids) ->
      data =
        posts: (makePosts ids)
      rsp.end (indexView.render data)
