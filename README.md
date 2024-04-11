# Storage Api

Этот сервис предназначен для учета складских остатков товаров запасных частей.


**У каждого товара имеются следующие данные:**
* наименование товара
* каталожный номер
* внутренний номер организации
* техника для которой применяется товар
* остаток товара
* стоимость товара

**Для товаров установленны следующие ограничения**
* товар не может не иметь названия
* товар не может не иметь внутреннего номера или иметь отрицательный номер 
* товар не может иметь остаток на складе меньше нуля
* товар не может иметь отрицательную цену

**В сервисе реализован следующий функционал:**

* По пути запроса "/product" 
---
* с использованием метода запроса GET - получение все товаров на складе; 
* с использованием метода запроса POST - добавление нового товара на склад;

* По пути запроса "/product/{id}"
---
* с использованием метода запроса GET - получение товара по уникальному идентификатору;
* с использованием метода запроса PATH - изменение товара по уникальному идентификатору;
* с использованием метода запроса DELETE - удаление товара по уникальному идентификатору;

# Запуск сервиса
Данное приложение написано без использования высокоуровневых фреймворков. 
Доступ к эндпойнтам осуществляется при поддержке технологии сервлетов, 
доступ к базе данных осуществляется по средствам использования JDBC.

#### Для запуска приложения
* В файле /resources/db.properties следует указать данные для подключения к базе данных, где:
  + db.url - путь подключения к базе данных
  + db.username - имя пользователя базы данных
  + db.password - пароль для подключения к базе данных 
* Убедитесь, что на вашем сервере имеется контейнер сервлетов (Tomcat, Jetty и подобные)
для корректного деплоя приложения.


## Приложение использует следующую ERD диаграмму по умолчанию:

![Class diagram](/docs/t_applicability.png)


## Контакты

Если Вас заинтересовала моя работа и Вы хотите со мной связаться:
* evgen986@mail.ru
* https://t.me/evgen04986

Благодарю за внимание!