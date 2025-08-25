![AskTheDev logo](./app/public/logo.png)

# Ask The Dev
An AI Chrome Extension for developers. AskTheDev reads the HTML of the webpage which you are on and sends it to OpenAI with your question. This allows developers to ask questions about the content they are visiting as if they were asking the creator themselves.

## Setup
1. Clone this repo.
2. Create a .env file in backend/AskTheDev/
3. Add your OpenAI and Stripe keys. Create a key for JWT_SECRET_KEY:
```
OPENAI_API_KEY=
OPENAI_PROJECT_ID=
OPENAI_ORG_ID=
STRIPE_API_KEY=
STRIPE_ENDPOINT_SECRET=
JWT_SECRET_KEY=
```
4. From backend/AskTheDev/ run ```mvn spring-boot:run```
5. Change directory to app/
6. Run ```npm run build```
7. Run ```npm run dev```
8. Open the localhost url printed to your terminal
9. Sign up and prompt away!
