export interface AuthUser {
  id: number
  username: string
  email: string
  sessionToken: string | null
}
