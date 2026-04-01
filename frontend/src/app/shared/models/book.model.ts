import { Chapter } from "./chapter.model";

export class Book {
    id: number;
    title: string;
    number: number;
    createdAt: Date;
    chapters: Chapter[];

    constructor(id: number, title: string, number: number, createdAt: Date, chapters: Chapter[]) {
        this.id = id;
        this.title = title;
        this.number = number;
        this.createdAt = createdAt;
        this.chapters = chapters;
    }
}