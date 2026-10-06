// Frontend development server only. API bodies and responses are forwarded unchanged.
const http = require('node:http');
const https = require('node:https');
const fs = require('node:fs');
const path = require('node:path');
const port = Number(process.env.PORT || 5173);
const backend = new URL(process.env.BACKEND_URL || 'http://localhost:8080');
if (!['http:', 'https:'].includes(backend.protocol)) throw new Error('BACKEND_URL must use http:// or https://');
const files = {'/':'index.html','/index.html':'index.html','/styles.css':'styles.css','/app.js':'app.js'};
const contentTypes = {'.html':'text/html; charset=utf-8','.css':'text/css; charset=utf-8','.js':'application/javascript; charset=utf-8'};
const server = http.createServer((req, res) => {
  const requestUrl = new URL(req.url, 'http://localhost');
  if (requestUrl.pathname.startsWith('/wmc/api/') || requestUrl.pathname.startsWith('/wmd/api/')) {
    const target = new URL(req.url, backend);
    const headers = {...req.headers, host:backend.host};
    // The browser talks to this frontend origin; the proxy makes the backend request.
    delete headers.origin;
    delete headers.referer;
    const transport = backend.protocol === 'https:' ? https : http;
    const upstream = transport.request(target, {method:req.method, headers}, response => {
      res.writeHead(response.statusCode, response.headers);
      response.pipe(res);
    });
    upstream.on('error', () => {
      if (res.headersSent) {res.destroy();return;}
      res.writeHead(502, {'Content-Type':'application/json'});
      res.end(JSON.stringify({message:`Frontend could not reach the backend at ${backend.origin}. Start the backend or set BACKEND_URL to its address.`,status:502,code:'FRONTEND_PROXY_ERROR'}));
    });
    upstream.setTimeout(30000, () => upstream.destroy(new Error('Backend request timed out')));
    req.on('aborted', () => upstream.destroy());
    res.on('close', () => {if (!res.writableEnded) upstream.destroy();});
    req.pipe(upstream);
    return;
  }
  if (requestUrl.pathname === '/favicon.ico') {res.writeHead(204).end();return;}
  const file = files[requestUrl.pathname];
  if (!file || !['GET','HEAD'].includes(req.method)) {res.writeHead(404).end('Not found');return;}
  fs.readFile(path.join(__dirname,file), (error,data) => {
    if(error){res.writeHead(500).end('Could not read UI file');return;}
    res.writeHead(200,{'Content-Type':contentTypes[path.extname(file)],'Cache-Control':'no-store'});
    res.end(req.method === 'HEAD' ? undefined : data);
  });
});
server.on('error', error => {console.error(`Frontend could not start: ${error.message}`);process.exitCode=1;});
server.listen(port,'127.0.0.1', () => {
  console.log(`WeMakeCoder frontend: http://localhost:${port}`);
  console.log(`API proxy forwards /wmc/api/* and /wmd/api/* to ${backend.origin}`);
  console.log('Keep the UI API base URL set to this frontend address. Enter an existing user UUID and load the home page.');
});
