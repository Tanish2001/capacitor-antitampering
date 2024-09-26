import { registerPlugin } from '@capacitor/core';
const AntiTampering = registerPlugin('AntiTampering', {
    web: () => import('./web').then(m => new m.AntiTamperingWeb()),
});
export * from './definitions';
export { AntiTampering };
//# sourceMappingURL=index.js.map