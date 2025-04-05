import { ReactElement } from "react";

export default function Logo(): ReactElement {

    return (
        <a target="_blank" href="https://askthedev.io">
            <img
                src="/logo.png"
                className="h-auto w-80 rounded-lg mb-10 dark:hidden"
            />
            <img
                src="/logo_dark.png"
                className="hidden h-auto w-80 rounded-lg mb-10 dark:block"
            />
        </a>
    );
}