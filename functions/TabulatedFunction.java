package functions;

public interface TabulatedFunction {
    // Методы получения границ области определения
    double getLeftDomainBorder();
    double getRightDomainBorder();
    
    // Метод получения значения функции
    double getFunctionValue(double x);
    
    // Методы работы с точками
    int getPointsCount();
    FunctionPoint getPoint(int index);
    void setPoint(int index, FunctionPoint point) throws InappropriateFunctionPointException;
    double getPointX(int index);
    void setPointX(int index, double x) throws InappropriateFunctionPointException;
    double getPointY(int index);
    void setPointY(int index, double y);
    void deletePoint(int index);
    void addPoint(FunctionPoint point) throws InappropriateFunctionPointException;
}