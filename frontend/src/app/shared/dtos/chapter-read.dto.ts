export interface ChapterReadDto {
    id: number;
    title: string;
    number: string;
    readAt: Date;
}

export interface ChapterReadCreateDto {
    chapterId: number;
    userId: number | null;
}