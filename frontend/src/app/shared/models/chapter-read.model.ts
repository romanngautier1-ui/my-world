import { Chapter } from "./chapter.model";
import { User } from "./user.model";

export class ChapterRead {
    id: number;
    user: User;
    chapter: Chapter;
    readAt: Date;

    constructor(id: number, user: User, chapter: Chapter, readAt: Date) {
        this.id = id;
        this.user = user;
        this.chapter = chapter;
        this.readAt = readAt;
    }
}