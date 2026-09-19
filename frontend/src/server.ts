import {
  AngularNodeAppEngine,
  createNodeRequestHandler,
  isMainModule,
  writeResponseToNodeResponse,
} from '@angular/ssr/node';
import express from 'express';
import { join } from 'node:path';

const browserDistFolder = join(import.meta.dirname, '../browser');

const app = express();
const angularApp = new AngularNodeAppEngine();
const apiTarget = process.env['API_TARGET'];

if (apiTarget) {
  app.use('/api', async (req, res, next) => {
    try {
      const targetUrl = new URL(req.originalUrl, apiTarget);
      const headers = new Headers(req.headers as Record<string, string>);

      headers.delete('host');
      headers.delete('content-length');

      const hasBody = !['GET', 'HEAD'].includes(req.method.toUpperCase());
      const response = await fetch(targetUrl, {
        method: req.method,
        headers,
        body: hasBody ? req : undefined,
        duplex: 'half',
      } as RequestInit & { duplex: 'half' });

      res.status(response.status);
      response.headers.forEach((value, key) => res.setHeader(key, value));

      if (response.body) {
        for await (const chunk of response.body) {
          res.write(chunk);
        }
      }

      res.end();
    } catch (error) {
      next(error);
    }
  });
}

/**
 * Serve static files from /browser
 */
app.use(
  express.static(browserDistFolder, {
    maxAge: '1y',
    index: false,
    redirect: false,
  }),
);

/**
 * Handle all other requests by rendering the Angular application.
 */
app.use((req, res, next) => {
  angularApp
    .handle(req)
    .then((response) =>
      response ? writeResponseToNodeResponse(response, res) : next(),
    )
    .catch(next);
});

/**
 * Start the server if this module is the main entry point, or it is ran via PM2.
 * The server listens on the port defined by the `PORT` environment variable, or defaults to 4000.
 */
if (isMainModule(import.meta.url) || process.env['pm_id']) {
  const port = process.env['PORT'] || 4000;
  app.listen(port, (error) => {
    if (error) {
      throw error;
    }

    console.log(`Node Express server listening on http://localhost:${port}`);
  });
}

/**
 * Request handler used by the Angular CLI (for dev-server and during build) or Firebase Cloud Functions.
 */
export const reqHandler = createNodeRequestHandler(app);
