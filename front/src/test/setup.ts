import '@testing-library/jest-dom/vitest';
import { cleanup } from '@testing-library/react';
import { afterEach } from 'vitest';

// Sin `globals: true` en vite.config.ts, React Testing Library no registra su
// limpieza automática: el DOM de un test sobrevive al siguiente y las consultas
// encuentran elementos del render anterior. Se limpia explícitamente.
afterEach(cleanup);

// jsdom no implementa showModal/close de <dialog> (sólo el atributo `open`
// reflejado): sin este polyfill, cualquier componente que abra un <dialog>
// con showModal() lanza "is not a function" en los tests.
if (!HTMLDialogElement.prototype.showModal) {
  HTMLDialogElement.prototype.showModal = function (this: HTMLDialogElement) {
    this.setAttribute('open', '');
  };
}
if (!HTMLDialogElement.prototype.close) {
  HTMLDialogElement.prototype.close = function (this: HTMLDialogElement) {
    this.removeAttribute('open');
    this.dispatchEvent(new Event('close'));
  };
}
