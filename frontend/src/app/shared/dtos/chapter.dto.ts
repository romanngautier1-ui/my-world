import { BookSummaryDto } from "./book.dto";
import { CommentDto } from "./comment.dto";

export interface ChapterDto {
  id: number;
  title: string;
  number: number;
  content: string;
  book: BookSummaryDto;
  createdAt: Date;
  comments: CommentDto[];
  like: number;
}

export interface ChapterListDto {
  id: number;
  title: string;
  number: number;
  createdAt: Date
}

export interface ChapterCreateDto {
    id: number;
    title: string;
    number: number;
    content?: string;
    bookId: number;
    uploadFile?: File;
}

export interface ChapterUpdateDto {
    title?: string;
  number?: number;
    content?: string;
    bookId?: number;
    uploadFile?: File;
}
