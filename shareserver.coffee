ss = (require '../sharejs').server
sc = (require '../sharejs').client
redis = (require 'redis').createClient()

splitType = (docName) ->
  lastColon = docName.lastIndexOf ':'
  return null if lastColon == -1
  [(docName.substring 0, lastColon), (docName.substring lastColon + 1)]

indexKey = (type) ->
  'index:' + type

module.exports.init = (app, server) ->
  # Create the model used by ShareJS and hook listeners to it for indexing.
  model = ss.createModel { type: 'redis' }

  model.addListener 'create', (docName, data) ->
    [type, name] = splitType docName
    redis.sadd (indexKey type), name
    console.log 'create "' + docName + '" : ' + type

  model.addListener 'delete', (docName) ->
    [type, name] = splitType docName
    redis.srem (indexKey type), name
    console.log 'delete "' + docName + '"'

  model.addListener 'applyOp', (docName, op, snapshot, oldSnapshot) ->
    [type, name] = splitType docName
    console.log docName + ' : ' + JSON.stringify(op) + ' : ' + type

  # Attach the sharejs REST and Socket.io interfaces to the server
  ss.attach server, { rest: true }, model

  # Simple index server.
  app.get '/index/:key', (req, rsp, next) ->
    redis.smembers (indexKey req.params.key), (err, replies) ->
      rsp.end JSON.stringify(replies)
