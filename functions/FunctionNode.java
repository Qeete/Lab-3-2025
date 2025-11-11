package functions;

// Внутренний класс элемента списка
class FunctionNode {
    // Информационное поле для хранения данных точки
    public FunctionPoint point;
    
    // Ссылки на предыдущий и следующий элемент
    public FunctionNode prev;
    public FunctionNode next;
    
    // Конструктор по умолчанию
    public FunctionNode() {
        this.point = null;
        this.prev = null;
        this.next = null;
    }
    
    // Конструктор с точкой
    public FunctionNode(FunctionPoint point) {
        this.point = point;
        this.prev = null;
        this.next = null;
    }
    
    // Конструктор с точкой и ссылками
    public FunctionNode(FunctionPoint point, FunctionNode prev, FunctionNode next) {
        this.point = point;
        this.prev = prev;
        this.next = next;
    }
}
