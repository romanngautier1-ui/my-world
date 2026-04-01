import { FormControl } from "@angular/forms";

export default class FormFilesUtils {
    static onFileSelected(event: Event, formControl: FormControl<File | null>) {
        const input = event.target as HTMLInputElement;
        if (input.files && input.files.length > 0) {
            const file = input.files[0];
            formControl.setValue(file);
        }
    }
}