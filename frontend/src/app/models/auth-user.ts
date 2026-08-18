// Authenticated user data returned by backend auth endpoints.
export interface AuthUser {
  // Database ID of the user account.
  id: number
  // Display username.
  username: string
  // Email used for login.
  email: string
  // Bearer token used for API authentication, or null when no session token is issued.
  sessionToken: string | null
}
