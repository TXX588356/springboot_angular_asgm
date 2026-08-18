import { mergeApplicationConfig, ApplicationConfig } from '@angular/core';
import { provideServerRendering, withRoutes } from '@angular/ssr';
import { appConfig } from './app.config';
import { serverRoutes } from './app.routes.server';

// Server-side rendering providers layered on top of the browser app configuration.
const serverConfig: ApplicationConfig = {
  providers: [
    provideServerRendering(withRoutes(serverRoutes))
  ]
};

// Combined configuration used by Angular SSR.
export const config = mergeApplicationConfig(appConfig, serverConfig);
