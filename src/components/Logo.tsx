import { ReactElement } from "react";
import logoSrc from "../../public/logo.png";

export default function Logo(): ReactElement {

    return (
        <img
            src={logoSrc}
            className="h-auto w-100 rounded-lg"
        ></img>
    );
}