import '@testing-library/jest-dom/vitest';
import { cleanup } from '@testing-library/react';
import { afterEach } from 'vitest';

// Sin `globals: true` en vite.config.ts, React Testing Library no registra su
// limpieza automática: el DOM de un test sobrevive al siguiente y las consultas
// encuentran elementos del render anterior. Se limpia explícitamente.
afterEach(cleanup);
