import { RenderMode, ServerRoute } from '@angular/ssr';

// Server render modes for dynamic routes and the prerendered fallback route.
export const serverRoutes: ServerRoute[] = [
  {
    path: 'foods/:id/edit',
    renderMode: RenderMode.Server
  },
  {
    path: 'foods/:id/nutrition',
    renderMode: RenderMode.Server
  },
  {
    path: 'foods/:id',
    renderMode: RenderMode.Server
  },
  {
    path: 'meal-plans/:id/edit',
    renderMode: RenderMode.Server
  },
  {
    path: 'meal-plans/:id',
    renderMode: RenderMode.Server
  },
  {
    path: '**',
    renderMode: RenderMode.Prerender
  }
];
