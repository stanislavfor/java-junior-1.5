# Java Junior (семинары)
## Урок 5. Клиент/Сервер своими руками
### Задание: 
Разработайте простой чат на основе сокетов. 
Ваше приложение должно включать в себя сервер, 
который принимает сообщения от клиентов и пересылает их всем участникам чата.

<br><br><hr><hr>

### Общая информация

Сокеты являются основным механизмом сетевого взаимодействия в Java, 
а сервер и клиент представляют собой две стороны этой коммуникации.

**Сокеты (Sockets)**

Сокеты - это механизм связи между процессами, используемый для обмена данными через сеть. 
В Java существуют два основных типа сокетов:

1. **TCP-сокеты**: обеспечивают надежную передачу данных по протоколу TCP (Transmission Control Protocol).
2. **UDP-сокеты**: передают данные по протоколу UDP (User Datagram Protocol), который менее надежен, но быстрее.

**Сервер (Server)**

Сервер - это программа, которая ожидает входящие соединения от клиентов и обрабатывает их запросы.
Сервер создает слушающий сокет (`ServerSocket`), ожидающий подключения клиентов.
После получения запроса от клиента сервер может обрабатывать данные, отправлять ответы и закрывать соединение.
Пример создания сервера на основе TCP-сокета, простого сервера, принимающего сообщения от клиентов:

```java

import java.net.*;
import java.io.*;

public class Server {
    public static void main(String[] args) throws IOException {
        // Создаем серверный сокет на порту 8080
        ServerSocket serverSocket = new ServerSocket(8080);
        
        while (true) {
            // Ожидаем подключения клиента
            Socket clientSocket = serverSocket.accept();
            
            // Обрабатываем запросы клиента
            BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
            PrintWriter out = new PrintWriter(clientSocket.getOutputStream(), true);
            
            String inputLine;
            while ((inputLine = in.readLine()) != null) {
                System.out.println("Получено сообщение: " + inputLine);
                
                // Отправляем ответ клиенту
                out.println("Сообщение получено");
                
                if ("exit".equals(inputLine)) {
                    break;
                }
            }
            
            // Закрываем соединение с клиентом
            clientSocket.close();
        }
    }
}
```

<br><br>

**Клиент (Client)**

Клиент - это программа, которая инициирует подключение к серверу и отправляет запросы. 
Клиенты используют `Socket` для установления соединения с сервером и отправки/получения данных.

Пример создания клиента:

```java
import java.net.*;
import java.io.*;

public class Client {
    public static void main(String[] args) throws IOException {
        // Устанавливаем соединение с сервером на порту 8080
        Socket socket = new Socket("localhost", 8080);
        
        // Открываем потоки ввода-вывода
        BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
        
        // Отправляем сообщение на сервер
        out.println("Привет, сервер!");
        
        // Получаем ответ от сервера
        String response = in.readLine();
        System.out.println("Ответ от сервера: " + response);
        
        // Закрываем соединение
        socket.close();
    }
}

```

<br><br><hr><hr>

### Инструкция

# Инструкция по использованию серверного и клиентского приложения на Java

## Описание приложения

Приложение представляет собой многопоточный чат-сервер ServerClass, <br>
принимающий подключения от клиентов ClientClass1, ClientClass2 и т. д., <br> 
которые могут отправлять и получать сообщения в общем чате. <br>
Можно использовать этот многопользовательский чат для обмена сообщениями через локальную сеть.

### Дополнительная информация

- Можно запустить несколько клиентов одновременно.
- Остановить сервер можно набрав команду `stop server` и нажав Enter в консоли запущенного класса ServerClass.
- При закрытии сервера все клиенты получат уведомление об отключении.

<br><hr>

## 1. Установка и запуск сервера

### 1.1 Требования к работе кода

- Java 8+
- Компилятор `javac`

### 1.2 Компиляция и запуск

1. Открыть терминал и перейти в папку с кодом:
   ```sh
   cd src/main/java/org/example/
   ```
2. Скомпилировать серверный код:
   ```sh
   javac -d . ServerClass.java
   ```
3. Запустить сервер:
   ```sh
   java org.example.ServerClass
   ```

   После запуска сервер будет ожидать подключения клиентов на порту `8080`.

<br><br>
<hr>

## 2. Установка и запуск клиента

### 2.1 Компиляция и запуск клиента

1. Открыть новый терминал и перейти в папку с кодом:
   ```sh
   cd src/main/java/org/example/
   ```
2. Скомпилировать клиентский код:
   ```sh
   javac -d . ClientClass1.java
   ```
3. Запустить клиента:
   ```sh
   java org.example.ClientClass1
   ```
4. Повторить для `ClientClass2`:
   ```sh
   cd src/main/java/org/example/
   javac -d . ClientClass2.java
   java org.example.ClientClass2
   ```
   и для `ClientClass3`, чтобы подключить несколько клиентов:
   ```sh
   cd src/main/java/org/example/
   javac -d . ClientClass3.java
   java org.example.ClientClass3
   ```
<br><br>
<hr>

## 3. Описание серверного кода (`ServerClass`)

### 3.1 Основные переменные

- `PORT = 8080` - порт для прослушивания входящих подключений.
- `clients = new ArrayList<>()` - список подключенных клиентов.
- `running = true` - состояние работы сервера.
- `serverSocket` - серверный сокет.

### 3.2 Основные методы

#### `start()` - запуск сервера

- Создаёт `ServerSocket` на `PORT`.
- Ожидает подключения клиентов.
- Запускает поток `readConsoleInput()` для обработки команд сервера.

#### `readConsoleInput()` - обработка команд

- Читает команды с консоли.
- Если введено `stop server`, останавливает сервер и уведомляет клиентов.

#### `broadcastMessage(String message)` - отправка сообщения всем клиентам

- Пересылает полученные от клиента сообщения остальным пользователям.

#### `stopServer()` - остановка сервера

- Закрывает все соединения и очищает список клиентов.

#### `ClientHandler` (вложенный класс)

- Отвечает за связь между сервером и отдельным клиентом.

##### Основные переменные

- `socket` - клиентский сокет.
- `in`, `out` - потоки ввода/вывода.
- `connected = true` - состояние подключения клиента.

##### Основные методы

- `run()` - принимает сообщения от клиента и передаёт их в `broadcastMessage()`.
- `sendMessage(String message)` - отправляет сообщение клиенту.
- `stopClient()` - закрывает соединение клиента.
- `closeConnections()` - закрывает потоки и удаляет клиента из списка.
<br><br>

### [Документация для Class ServerSocket](https://docs.oracle.com/en/java/javase/11/docs/api/java.base/java/net/ServerSocket.html)

<br><br>
<hr>

## 4. Описание клиентского кода (`ClientClass1`)

### 4.1 Основные переменные

- `socket` - клиентский сокет.
- `out`, `in` - потоки ввода/вывода.
- `loggedIn = false` - состояние авторизации.
- `username` - имя пользователя.
- `GUI` - графический интерфейс (Swing).

### 4.2 Основные методы

#### `connectToServer()` - подключение к серверу

- Открывает `Socket` на `localhost:8080`.
- Создаёт потоки для обмена сообщениями.
- Запускает `readMessages()` в отдельном потоке.

#### `readMessages()` - приём сообщений с сервера

- Читает входящие сообщения и обновляет `chatArea`.

#### `disconnectFromServer()` - отключение клиента

- Закрывает потоки и сокет соединения.

#### `actionPerformed(ActionEvent e)` - обработка кнопок

- `login` - подключает клиента к серверу.
- `post` - отправляет сообщение.
- `delete` - очищает поле ввода.
- `exit` - закрывает соединение и завершает программу.
<br><br>

### [Документация для Class Socket](https://docs.oracle.com/en/java/javase/11/docs/api/java.base/java/net/Socket.html)

<br><br>
<hr>

## 5. Описание графического интерфейса клиента

### 5.1 Используемые компоненты
- `JFrame` - основное окно приложения. [Документация для Class JFrame](https://docs.oracle.com/en/java/javase/11/docs/api/java.desktop/javax/swing/JFrame.html)
- `JTextField` - поле для ввода текста. [Документация для Class JTextField](https://docs.oracle.com/en/java/javase/11/docs/api/java.desktop/javax/swing/JTextField.html)
- `JButton` - кнопки управления. [Документация для Class JButton](https://docs.oracle.com/en/java/javase/11/docs/api/java.desktop/javax/swing/JButton.html)
- `JTextArea` - область чата. [Документация для Class JTextArea](https://docs.oracle.com/en/java/javase/11/docs/api/java.desktop/javax/swing/JTextArea.html)
- `JScrollPane` - скроллбар для текстовой области. [Документация для Class JScrollPane](https://docs.oracle.com/en/java/javase/11/docs/api/java.desktop/javax/swing/JScrollPane.html)

### 5.2 Размещение компонентов
- Верхняя панель (`JPanel`): поле ввода имени (`JTextField`), кнопка `login`.
- Центральная область: `JTextArea` для отображения чата, вложенная в `JScrollPane`.
- Нижняя панель (`JPanel`): поле ввода сообщения (`JTextField`), кнопки `post`, `delete`, `exit`.

### 5.3 Обработчики событий
- `KeyAdapter` - активация кнопки `login`, если введён текст в `usernameField`.
- `ActionListener` - обработка нажатий кнопок.
- `Thread` - асинхронное получение сообщений с сервера.

<br><br>
<hr>

## 6. Взаимодействие сокетов в сервере и клиентах

### 6.1 Последовательность работы классов

1. **Сервер `ServerClass`** создаёт `ServerSocket` и ждёт подключений.
2. **Клиенты `ClientClass1` и др.** создают `Socket` и подключаются к серверу.
3. **При подключении клиента** сервер создаёт `ClientHandler`, который обрабатывает его сообщения.
4. **Клиент отправляет сообщения** через `PrintWriter`, сервер принимает их и рассылает остальным клиентам через `broadcastMessage()`.
5. **При отключении клиента** его соединение закрывается, а сервер удаляет его из списка клиентов.
6. **При закрытии сервера** отправляется сообщение об остановке и закрываются все соединения.

### 6.2 Пользовательская инструкция

- После запуска консоли сервера и графического интерфейса клиентов, необходимо авторизоваться в клиенте. 
- В верхнем поле для ввода указать имя пользователя кириллицей или латинскими буквами и нажать кнопку `login`. 
- Для отправки сообщений в общий чат нажать кнопку `post`. 
- Отменить ввод сообщения можно нажатием кнопки `delete`. 
- Для выхода из чата, нажать кнопку `exit` в клиенте.
- Отключить работу сервера можно набрав команду `stop server` в терминале сервера и нажать Enter.

<br><br><br><br>
<br><br><hr><hr>

![](assets/java-grey-logo.png)

<hr><hr><br><br>