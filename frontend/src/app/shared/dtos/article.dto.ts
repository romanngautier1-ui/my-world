export interface ArticleListDto {
    id: number;
    title: string;
    createdAt: Date;
    urlImage: string;
}

export interface ArticleDto {
    id: number;
    title: string;
    content: string;
    username: string;
    urlImage: string;
    createdAt: Date;
}

export interface ArticleCreateDto {
    id: number;
    title: string;
    content: string;
    userId: number | null;
    uploadImage?: File;
    urlImage?: string;
}

export interface ArticleUpdateDto {
    title?: string;
    content?: string;
    urlImage?: string;
    uploadImage?: File;
}