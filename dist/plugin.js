var capacitorAntiTampering = (function (exports, core) {
    'use strict';

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

    Object.defineProperty(exports, '__esModule', { value: true });

    return exports;

})({}, capacitorExports);
//# sourceMappingURL=plugin.js.map
