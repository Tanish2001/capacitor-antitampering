import { registerPlugin } from '@capacitor/core';

import type { AntiTamperingPlugin } from './definitions';

const AntiTampering = registerPlugin<AntiTamperingPlugin>('AntiTampering', {
  web: () => import('./web').then(m => new m.AntiTamperingWeb()),
});

export * from './definitions';
export { AntiTampering };
