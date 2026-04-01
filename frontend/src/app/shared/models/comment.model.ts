import { Chapter } from "./chapter.model";
import { User } from "./user.model";

export class Comment {
    id: number;
    content: string;
    rating: number;
    user: User;
    chapter: Chapter;
    createdAt: Date;

    constructor(id: number, content: string, rating: number, chapter: Chapter, user: User, createdAt: Date) {
        this.id = id;
        this.content = content;
        this.rating = rating;
        this.chapter = chapter;
        this.user = user;
        this.createdAt = createdAt;
    }
}