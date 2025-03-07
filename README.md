# Ask The Dev
An AI Chrome Extension for developers. AskTheDev reads the HTML of the webpage which you are on and sends it to OpenAI with your question. This allows developers to ask questions about the content they are visiting as if they were asking the creator themselves.

## Setup
1. Clone this repo.
2. Create a .env file in the root directory of this project.
3. Paste the following into .env: `VITE_OPENAI_API_KEY=<Your OpenAI api key>`.
4. Install the dependencies: `npm install`.
5. Run the following to create the build directory: `npm run build`. If you want to run a dev instance locally, enter: `npm run dev`.
6. Go to Chrome and go to Extensions. Here you must enable "Developer mode".
7. Click "Load Unpacked" and select the build folder.
8. Pin the extension to your extension bar and ask away!
