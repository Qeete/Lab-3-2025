package functions;

public class LinkedListTabulatedFunction implements TabulatedFunction{
    // Голова списка (не содержит данных, всегда существует)
    private FunctionNode head;
    
    // Количество значащих элементов (без головы)
    private int pointsCount;
    
    // Вспомогательные поля для оптимизации доступа
    private FunctionNode lastAccessedNode;
    private int lastAccessedIndex;
    
    // Конструктор по умолчанию (пустой список)
    public LinkedListTabulatedFunction() {
        // Создаем голову, которая ссылается сама на себя
        head = new FunctionNode();
        head.prev = head;
        head.next = head;
        pointsCount = 0;
        lastAccessedNode = head;
        lastAccessedIndex = -1;
    }
    
    // Конструктор с параметрами (равномерная сетка)
    public LinkedListTabulatedFunction(double leftX, double rightX, int pointsCount) {
        this(); // Вызываем конструктор по умолчанию для инициализации головы
        
        // Проверка условий конструктора
        if (leftX >= rightX) {
            throw new IllegalArgumentException("Левая граница должна быть меньше правой");
        }
        if (pointsCount < 2) {
            throw new IllegalArgumentException("Количество точек должно быть не менее двух");
        }
        
        // Создаем точки равномерной сетки
        double step = (rightX - leftX) / (pointsCount - 1);
        for (int i = 0; i < pointsCount; i++) {
            double x = leftX + i * step;
            addNodeToTail().point = new FunctionPoint(x, 0);
        }
    }
    
    // Конструктор с параметрами (массив значений)
    public LinkedListTabulatedFunction(double leftX, double rightX, double[] values) {
        this(); // Вызываем конструктор по умолчанию для инициализации головы
        
        // Проверка условий конструктора
        if (leftX >= rightX) {
            throw new IllegalArgumentException("Левая граница должна быть меньше правой");
        }
        if (values.length < 2) {
            throw new IllegalArgumentException("Количество точек должно быть не менее двух");
        }
        
        // Создаем точки с заданными значениями
        double step = (rightX - leftX) / (values.length - 1);
        for (int i = 0; i < values.length; i++) {
            double x = leftX + i * step;
            addNodeToTail().point = new FunctionPoint(x, values[i]);
        }
    }
    
    // Метод для получения узла по индексу с оптимизацией доступа
    private FunctionNode getNodeByIndex(int index) {
        // Проверка корректности индекса
        if (index < 0 || index >= pointsCount) {
            throw new FunctionPointIndexOutOfBoundsException("Некорректный индекс: " + index);
        }
        
        // Оптимизация: если запрашиваем тот же элемент, что и в прошлый раз
        if (lastAccessedIndex == index) {
            return lastAccessedNode;
        }
        
        // Оптимизация: если запрашиваем следующий элемент
        if (lastAccessedIndex != -1 && lastAccessedIndex == index - 1) {
            lastAccessedNode = lastAccessedNode.next;
            lastAccessedIndex = index;
            return lastAccessedNode;
        }
        
        // Оптимизация: если запрашиваем предыдущий элемент
        if (lastAccessedIndex != -1 && lastAccessedIndex == index + 1) {
            lastAccessedNode = lastAccessedNode.prev;
            lastAccessedIndex = index;
            return lastAccessedNode;
        }
        
        // Поиск с ближайшего конца
        FunctionNode current;
        int currentIndex;
        
        if (index < pointsCount / 2) {
            // Ищем с начала
            current = head.next;
            currentIndex = 0;
            while (currentIndex < index) {
                current = current.next;
                currentIndex++;
            }
        } else {
            // Ищем с конца
            current = head.prev;
            currentIndex = pointsCount - 1;
            while (currentIndex > index) {
                current = current.prev;
                currentIndex--;
            }
        }
        
        // Сохраняем для будущей оптимизации
        lastAccessedNode = current;
        lastAccessedIndex = index;
        
        return current;
    }
    
    // Метод для добавления узла в конец списка
    private FunctionNode addNodeToTail() {
        FunctionNode newNode = new FunctionNode();
        
        // Вставляем перед головой (что эквивалентно концу списка)
        newNode.prev = head.prev;
        newNode.next = head;
        
        head.prev.next = newNode;
        head.prev = newNode;
        
        pointsCount++;
        lastAccessedNode = newNode;
        lastAccessedIndex = pointsCount - 1;
        
        return newNode;
    }
    
    // Метод для добавления узла по индексу
    private FunctionNode addNodeByIndex(int index) {
        if (index < 0 || index > pointsCount) {
            throw new FunctionPointIndexOutOfBoundsException("Некорректный индекс для вставки: " + index);
        }
        
        if (index == pointsCount) {
            // Вставка в конец
            return addNodeToTail();
        }
        
        // Находим узел, перед которым нужно вставить новый
        FunctionNode nextNode = getNodeByIndex(index);
        FunctionNode prevNode = nextNode.prev;
        
        // Создаем новый узел
        FunctionNode newNode = new FunctionNode();
        newNode.prev = prevNode;
        newNode.next = nextNode;
        
        // Обновляем ссылки соседних узлов
        prevNode.next = newNode;
        nextNode.prev = newNode;
        
        pointsCount++;
        lastAccessedNode = newNode;
        lastAccessedIndex = index;
        
        return newNode;
    }
    
    // Метод для удаления узла по индексу
    private FunctionNode deleteNodeByIndex(int index) {
        if (index < 0 || index >= pointsCount) {
            throw new FunctionPointIndexOutOfBoundsException("Некорректный индекс для удаления: " + index);
        }
        
        if (pointsCount < 3) {
            throw new IllegalStateException("Невозможно удалить точку: должно остаться минимум 2 точки");
        }
        
        // Находим удаляемый узел
        FunctionNode nodeToDelete = getNodeByIndex(index);
        
        // Обновляем ссылки соседних узлов
        nodeToDelete.prev.next = nodeToDelete.next;
        nodeToDelete.next.prev = nodeToDelete.prev;
        
        pointsCount--;
        
        // Сбрасываем кэш, если удалили кэшированный элемент
        if (lastAccessedIndex == index) {
            lastAccessedNode = head;
            lastAccessedIndex = -1;
        } else if (lastAccessedIndex > index) {
            lastAccessedIndex--;
        }
        
        return nodeToDelete;
    }
    
    // Методы табулированной функции (аналогичные TabulatedFunction)
    
    public double getLeftDomainBorder() {
        if (pointsCount == 0) {
            return Double.NaN;
        }
        return head.next.point.getX();
    }
    
    public double getRightDomainBorder() {
        if (pointsCount == 0) {
            return Double.NaN;
        }
        return head.prev.point.getX();
    }
    
    public double getFunctionValue(double x) {
        if (pointsCount == 0 || x < getLeftDomainBorder() || x > getRightDomainBorder()) {
            return Double.NaN;
        }
        
        FunctionNode current = head.next;
        
        // Проверяем первую точку
        if (x == current.point.getX()) {
            return current.point.getY();
        }
        
        // Ищем интервал
        for (int i = 0; i < pointsCount - 1; i++) {
            double curX = current.point.getX();
            double nextX = current.next.point.getX();
            
            if (x == nextX) {
                return current.next.point.getY();
            }
            
            if (x > curX && x < nextX) {
                return linearInterpolation(current.point, current.next.point, x);
            }
            
            current = current.next;
        }
        
        return Double.NaN;
    }
    private double linearInterpolation(FunctionPoint p1, FunctionPoint p2, double x) {
        double x1 = p1.getX();
        double y1 = p1.getY();
        double x2 = p2.getX();
        double y2 = p2.getY();
        
        return y1 + (y2 - y1) * (x - x1) / (x2 - x1);
    }
    
    public int getPointsCount() {
        return pointsCount;
    }
    
    public FunctionPoint getPoint(int index) {
        return new FunctionPoint(getNodeByIndex(index).point);
    }
    
    public void setPoint(int index, FunctionPoint point) throws InappropriateFunctionPointException {
        FunctionNode node = getNodeByIndex(index);
        
        // Проверка упорядоченности точек
        if (index > 0 && point.getX() <= getNodeByIndex(index - 1).point.getX()) {
            throw new InappropriateFunctionPointException("X координата точки должна быть больше предыдущей");
        }
        if (index < pointsCount - 1 && point.getX() >= getNodeByIndex(index + 1).point.getX()) {
            throw new InappropriateFunctionPointException("X координата точки должна быть меньше следующей");
        }
        
        node.point = new FunctionPoint(point);
    }
    
    public double getPointX(int index) {
        return getNodeByIndex(index).point.getX();
    }
    
    public void setPointX(int index, double x) throws InappropriateFunctionPointException {
        FunctionNode node = getNodeByIndex(index);
        
        // Проверка упорядоченности точек
        if (index > 0 && x <= getNodeByIndex(index - 1).point.getX()) {
            throw new InappropriateFunctionPointException("X координата должна быть больше предыдущей");
        }
        if (index < pointsCount - 1 && x >= getNodeByIndex(index + 1).point.getX()) {
            throw new InappropriateFunctionPointException("X координата должна быть меньше следующей");
        }
        
        node.point.setX(x);
    }
    
    public double getPointY(int index) {
        return getNodeByIndex(index).point.getY();
    }
    
    public void setPointY(int index, double y) {
        getNodeByIndex(index).point.setY(y);
    }
    
    public void deletePoint(int index) {
        deleteNodeByIndex(index);
    }
    
    public void addPoint(FunctionPoint point) throws InappropriateFunctionPointException {
        double newX = point.getX();
        
        // Проверяем, не существует ли уже точка с таким X
        FunctionNode current = head.next;
        for (int i = 0; i < pointsCount; i++) {
            if (current.point.getX() == newX) {
                throw new InappropriateFunctionPointException("Точка с таким X уже существует");
            }
            current = current.next;
        }
        
        // Находим позицию для вставки
        int insertIndex = 0;
        current = head.next;
        while (insertIndex < pointsCount && current.point.getX() < newX) {
            current = current.next;
            insertIndex++;
        }
        
        // Вставляем новую точку
        FunctionNode newNode = addNodeByIndex(insertIndex);
        newNode.point = new FunctionPoint(point);
    }
    

}