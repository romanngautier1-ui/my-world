export interface Email {
  recipient: string;
  subject: string;
  message: string;
  attachment?: string;
}