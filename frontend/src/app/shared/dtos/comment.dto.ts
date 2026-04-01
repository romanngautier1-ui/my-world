export interface CommentDto {
    id: number;
    content: string;
    rating: number;
    username: string;
    createdAt: Date;
}

export interface CommentCreateDto {
    content: string;
    rating: number;
    userId: number;
    chapterId: number;
}

export interface CommentUpdateDto {
    content?: string;
    rating?: number;
}