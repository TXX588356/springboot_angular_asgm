import { RenderMode, ServerRoute } from '@angular/ssr';

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
    path: '**',
    renderMode: RenderMode.Prerender
  }
];
