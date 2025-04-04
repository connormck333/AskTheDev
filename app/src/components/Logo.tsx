import { ReactElement } from "react";

export default function Logo(): ReactElement {

    return (
        <a href="https://askthedev.io">
            <img
                src="/logo.png"
                className="h-auto w-80 rounded-lg pb-10"
            />
        </a>
    );
}