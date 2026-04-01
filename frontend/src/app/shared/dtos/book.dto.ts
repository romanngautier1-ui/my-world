import { ChapterListDto } from "./chapter.dto";

export interface BookDto {
    id: number;
    title: string;
    number: number;
    description: string;
    createdAt: Date;
    chapters: ChapterListDto[];
    urlImage: string;
}

export interface BookListDto {
    id: number;
    title: string;
    number: number;
    description: string;
    createdAt: Date;
    chapterCount: number;
    urlImage: string;
}

export interface BookCreateDto {
    title: string;
    number: number;
    description: string;
    uploadImage?: File;
    urlImage?: string;
}

export interface BookUpdateDto {
    title?: string;
    number?: number;
    description?: string;
    uploadImage?: File;
    urlImage?: string;
}

export interface BookSummaryDto {
    id: number;
    title: string;
    number: number;
}