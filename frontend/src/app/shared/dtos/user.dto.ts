export interface UserChapterReadDto {
    chapterId: number;
    chapterTitle: string;
    bookNumber: number;
    readAt: Date;
}

export interface UserDto {
    id: number;
    username: string;
    email: string;
    isActive: boolean;
    createdAt: Date;
    lastReads: UserChapterReadDto[];
}

export interface UserCreateDto {
    email: string;
    username: string;
    password: string;
}

export interface UserUpdateDto {
    email?: string;
    username?: string;
    password?: string;
}