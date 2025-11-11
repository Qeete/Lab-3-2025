import functions.TabulatedFunction;
import functions.ArrayTabulatedFunction;
import functions.LinkedListTabulatedFunction;
import functions.FunctionPoint;
import functions.FunctionPointIndexOutOfBoundsException;
import functions.InappropriateFunctionPointException;

public class Main {
    public static void main(String[] args) {
        
        System.out.println("=== ТЕСТИРОВАНИЕ ARRAY TABULATED FUNCTION ===");
        testFunction(new ArrayTabulatedFunction(0, 4, new double[]{0, 1, 4, 9, 16}));
        
        System.out.println("\n=== ТЕСТИРОВАНИЕ LINKED LIST TABULATED FUNCTION ===");
        testFunction(new LinkedListTabulatedFunction(0, 4, new double[]{0, 1, 4, 9, 16}));
        
        System.out.println("\n=== ТЕСТИРОВАНИЕ ИСКЛЮЧЕНИЙ В КОНСТРУКТОРАХ ===");
        testConstructorExceptions();
    }
    
    public static void testFunction(TabulatedFunction function) {
        String functionType = function.getClass().getSimpleName();
        
        // 1. Нормальная работа функции
        System.out.println("\n1. Нормальная работа " + functionType);
        System.out.println("Количество точек: " + function.getPointsCount());
        System.out.println("Область определения: [" + function.getLeftDomainBorder() + ", " + function.getRightDomainBorder() + "]");
        
        // Вычисление значений
        double[] testPoints = {-1, 0, 0.5, 2, 4, 5};
        for (double x : testPoints) {
            double y = function.getFunctionValue(x);
            if (Double.isNaN(y)) {
                System.out.printf("f(%.1f) = не определено (вне области)\n", x);
            } else {
                System.out.printf("f(%.1f) = %.2f\n", x, y);
            }
        }
        
        // 2. Тестирование исключений при работе с точками
        System.out.println("\n2. Тестирование исключений " + functionType);
        
        // 2.1. FunctionPointIndexOutOfBoundsException - неверные индексы
        System.out.println("2.1. FunctionPointIndexOutOfBoundsException:");
        try {
            function.getPoint(-1);
            System.out.println("Исключение не было выброшено для индекса -1");
        } catch (FunctionPointIndexOutOfBoundsException e) {
            System.out.println("Выброшено исключение для индекса -1: " + e.getMessage());
        }
        
        try {
            function.getPoint(10);
            System.out.println("Исключение не было выброшено для индекса 10");
        } catch (FunctionPointIndexOutOfBoundsException e) {
            System.out.println("Выброшено исключение для индекса 10: " + e.getMessage());
        }
        
        try {
            function.setPointY(-1, 5.0);
            System.out.println("Исключение не было выброшено для индекса -1 в setPointY");
        } catch (FunctionPointIndexOutOfBoundsException e) {
            System.out.println("Выброшено исключение для setPointY с индексом -1: " + e.getMessage());
        }
        
        // 2.2. InappropriateFunctionPointException - нарушение упорядоченности
        System.out.println("\n2.2. InappropriateFunctionPointException:");
        try {
            function.setPoint(2, new FunctionPoint(0.5, 10.0)); // X меньше предыдущего
            System.out.println("Исключение не было выброшено при нарушении упорядоченности (меньше предыдущего)");
        } catch (InappropriateFunctionPointException e) {
            System.out.println("Выброшено исключение при X меньше предыдущего: " + e.getMessage());
        }
        
        try {
            function.setPoint(1, new FunctionPoint(3.0, 10.0)); // X больше следующего
            System.out.println("Исключение не было выброшено при нарушении упорядоченности (больше следующего)");
        } catch (InappropriateFunctionPointException e) {
            System.out.println("Выброшено исключение при X больше следующего: " + e.getMessage());
        }
        
        try {
            function.addPoint(new FunctionPoint(2.0, 10.0)); // Точка с существующим X
            System.out.println("Исключение не было выброшено при добавлении точки с существующим X");
        } catch (InappropriateFunctionPointException e) {
            System.out.println("Выброшено исключение при добавлении точки с существующим X: " + e.getMessage());
        }
        
        // 2.3. IllegalStateException - удаление при малом количестве точек
        System.out.println("\n2.3. IllegalStateException:");
        // Сначала удалим точки до минимального количества
        while (function.getPointsCount() > 3) {
            function.deletePoint(function.getPointsCount() - 1);
        }
        
        try {
            function.deletePoint(0); // Попытка удалить точку когда осталось 3
            System.out.println("Успешно удалена точка при pointsCount = 3");
            
            // Теперь осталось 2 точки - следующее удаление должно вызвать исключение
            function.deletePoint(0);
            System.out.println("Исключение не было выброшено при удалении последней точки");
        } catch (IllegalStateException e) {
            System.out.println("Выброшено исключение при попытке удалить точку когда pointsCount < 3: " + e.getMessage());
        } catch (FunctionPointIndexOutOfBoundsException e) {
            System.out.println("Выброшено исключение при неверном индексе: " + e.getMessage());
        }
        
        System.out.println("\nТестирование " + functionType + " завершено");
    }
    
    public static void testConstructorExceptions() {
        // Тестирование исключений в конструкторах
        
        System.out.println("1. IllegalArgumentException - некорректные границы:");
        try {
            TabulatedFunction func = new ArrayTabulatedFunction(5, 5, 3); // leftX == rightX
            System.out.println("Исключение не было выброшено при leftX == rightX");
        } catch (IllegalArgumentException e) {
            System.out.println("ArrayTabulatedFunction: Выброшено исключение при leftX == rightX: " + e.getMessage());
        }
        
        try {
            TabulatedFunction func = new LinkedListTabulatedFunction(5, 5, 3); // leftX == rightX
            System.out.println("Исключение не было выброшено при leftX == rightX");
        } catch (IllegalArgumentException e) {
            System.out.println("LinkedListTabulatedFunction: Выброшено исключение при leftX == rightX: " + e.getMessage());
        }
        
        try {
            TabulatedFunction func = new ArrayTabulatedFunction(6, 5, 3); // leftX > rightX
            System.out.println("Исключение не было выброшено при leftX > rightX");
        } catch (IllegalArgumentException e) {
            System.out.println("ArrayTabulatedFunction: Выброшено исключение при leftX > rightX: " + e.getMessage());
        }
        
        System.out.println("\n2. IllegalArgumentException - недостаточное количество точек:");
        try {
            TabulatedFunction func = new ArrayTabulatedFunction(0, 5, 1); // pointsCount < 2
            System.out.println("Исключение не было выброшено при pointsCount < 2");
        } catch (IllegalArgumentException e) {
            System.out.println("ArrayTabulatedFunction: Выброшено исключение при pointsCount < 2: " + e.getMessage());
        }
        
        try {
            TabulatedFunction func = new LinkedListTabulatedFunction(0, 5, new double[]{1}); // массив длины 1
            System.out.println("Исключение не было выброшено при массиве длины < 2");
        } catch (IllegalArgumentException e) {
            System.out.println("LinkedListTabulatedFunction: Выброшено исключение при массиве длины < 2: " + e.getMessage());
        }
        
    }
}