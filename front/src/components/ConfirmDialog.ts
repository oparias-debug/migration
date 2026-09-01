import Swal from 'sweetalert2';

interface ConfirmDialogOptions {
  confirmButtonText?: string;
  cancelButtonText?: string;
}

// Réplica de la ventana de confirmación en app.js (.form-confirmar submit handler).
export async function confirmDialog(message: string, options?: ConfirmDialogOptions): Promise<boolean> {
  const result = await Swal.fire({
    title: 'Confirmación',
    text: message,
    icon: 'warning',
    showCancelButton: true,
    confirmButtonText: options?.confirmButtonText ?? 'Sí',
    cancelButtonText: options?.cancelButtonText ?? 'Cancelar',
  });
  return result.isConfirmed;
}
