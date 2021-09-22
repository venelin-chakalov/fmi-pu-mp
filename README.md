# fmi-pu-mp

# Списък с контакти

---
Чрез това приложение можете да създавате, редактирате и премахвате контакти.
Освен основните CRUD операции можете да звъните и да изпращате SMS към избрани номера.


За база данни се използва SQLite.
В приложението също има интегриран API, който показва информация (флаг, валута и име) относно държава към която този номер принадлежи. Приложението разбира държавата на даден номер чрез телефония код.

Линк към API-а, който е използван: https://country-cities.herokuapp.com/

Приложението комуникира с вградените "Activity"-та, които предоставя възможност за набиране на номер и изпращане на SMS към избран номер.


Приложението е тествано на Pixel 2 API 24.
