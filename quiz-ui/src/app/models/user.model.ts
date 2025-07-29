import { Role } from "./role.model";

export interface User {
  id?: number;
  username: string;
  password?: string; // Optional for DTOs like login/register
  role: Role;
  token?: string; // JWT token
}
