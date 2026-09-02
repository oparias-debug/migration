import { beforeEach, describe, expect, it, vi } from 'vitest';
import { fireEvent, render, screen, waitFor } from '@testing-library/react';
import { MemoryRouter } from 'react-router-dom';
import '../i18n/i18n';

const login = vi.fn();
const navigate = vi.fn();

vi.mock('../auth/useAuth', () => ({ useAuth: () => ({ login }) }));
vi.mock('react-router-dom', async (importOriginal) => ({
  ...(await importOriginal<typeof import('react-router-dom')>()),
  useNavigate: () => navigate,
}));

const { LoginPage } = await import('./LoginPage');

const montar = () => render(<MemoryRouter><LoginPage /></MemoryRouter>);

describe('LoginPage', () => {
  beforeEach(() => { login.mockReset(); navigate.mockReset(); });

  it('muestra los logotipos oficiales del Ministerio y del SIIP', () => {
    montar();
    expect(screen.getByAltText(/Ministerio de Hacienda/i)).toBeInTheDocument();
    expect(screen.getByAltText(/Sistema de Información de Inversión Pública/i)).toBeInTheDocument();
  });

  it('entra con usuario y contraseña', async () => {
    login.mockResolvedValue(undefined);
    montar();
    fireEvent.change(screen.getByLabelText('Usuario'), { target: { value: 'tecnico.urp' } });
    fireEvent.change(screen.getByLabelText('Contraseña'), { target: { value: 'secreto' } });
    fireEvent.click(screen.getByRole('button', { name: 'Iniciar sesión' }));
    await waitFor(() => expect(login).toHaveBeenCalledWith('tecnico.urp', 'secreto'));
    await waitFor(() => expect(navigate).toHaveBeenCalledWith('/'));
  });

  it('avisa cuando las credenciales no son válidas', async () => {
    login.mockRejectedValue(new Error('401'));
    montar();
    fireEvent.change(screen.getByLabelText('Usuario'), { target: { value: 'x' } });
    fireEvent.change(screen.getByLabelText('Contraseña'), { target: { value: 'y' } });
    fireEvent.click(screen.getByRole('button', { name: 'Iniciar sesión' }));
    expect(await screen.findByRole('alert')).toBeInTheDocument();
    expect(navigate).not.toHaveBeenCalled();
  });

  it('el ojo alterna entre ocultar y mostrar la contraseña', () => {
    montar();
    const campo = screen.getByLabelText('Contraseña');
    expect(campo).toHaveAttribute('type', 'password');
    fireEvent.click(screen.getByRole('button', { name: 'Mostrar contraseña' }));
    expect(campo).toHaveAttribute('type', 'text');
  });

  // Los tres elementos del diseño que todavía no tienen respaldo en el backend
  // se muestran, pero deshabilitados: que se vean no debe hacer creer que funcionan.
  it('deja inactivos los elementos sin backend (EN, reCAPTCHA, olvidó su contraseña)', () => {
    montar();
    expect(screen.getByRole('button', { name: 'EN' })).toBeDisabled();
    expect(screen.getByRole('button', { name: '¿Olvidó su contraseña?' })).toBeDisabled();
    expect(screen.getByRole('note')).toBeInTheDocument();
  });
});
