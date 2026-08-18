// Payload sent when logging in.
export interface LoginRequest {
  // Email used as the login principal.
  email: string
  // Plain-text password submitted for authentication.
  password: string
}
