'use strict';

Object.defineProperty(exports, '__esModule', { value: true });

var core = require('@capacitor/core');

const AntiTampering = core.registerPlugin('AntiTampering', {
    web: () => Promise.resolve().then(function () { return web; }).then(m => new m.AntiTamperingWeb()),
});

class AntiTamperingWeb extends core.WebPlugin {
    async verify() {
        throw new Error('Method not implemented.');
    }
}

var web = /*#__PURE__*/Object.freeze({
    __proto__: null,
    AntiTamperingWeb: AntiTamperingWeb
});

exports.AntiTampering = AntiTampering;
//# sourceMappingURL=plugin.cjs.js.map
