import { createContext } from "react";
import { ScrollContainer } from "../utils/interfaces";

const ScrollContainerContext = createContext<ScrollContainer | null>(null);

export default ScrollContainerContext;