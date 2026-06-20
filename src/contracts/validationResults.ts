import { ValidationResult } from './types/ValidationResult'

export const defaultResult: ValidationResult = {
  decision: undefined,
  reason: '',
}

export function block(reason: string): ValidationResult {
  return {
    decision: 'block',
    reason,
  }
}
