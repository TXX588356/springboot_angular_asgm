// Payload sent when registering a new account.
export interface RegisterRequest {
  // Display username chosen by the user.
  username: string
  // Email used for login.
  email: string
  // Plain-text password submitted for account creation.
  password: string
}
