package functions;
import java.util.Iterator;
public class LinkedListTabulatedFunction implements TabulatedFunction {
    // Внутренний класс элемента списка
    private static class FunctionNode {
        FunctionPoint point;      // Данные точки
        FunctionNode prev;        // Ссылка на предыдущий элемент
        FunctionNode next;        // Ссылка на следующий элемент
        // Конструктор
        FunctionNode(FunctionPoint point) {
            this.point = point;
        }
    }
    private FunctionNode head; //Голова списка
    private int pointsCount; //Количество точек в списке
    private FunctionNode getNodeByIndex(int index) {
        if (index < 0 || index >= pointsCount) {
            throw new FunctionPointIndexOutOfBoundsException("Индекс выходит за границы: " + index);
        }
        FunctionNode current;
        // Выбор оптимального направления обхода
        if (index < pointsCount / 2) {
            // Ищем с начала (если индекс в первой половине)
            current = head.next;
            for (int i = 0; i < index; i++) {
                current = current.next;
            }
        } else {
            // Ищем с конца (если индекс во второй половине)
            current = head.prev;
            for (int i = pointsCount - 1; i > index; i--) {
                current = current.prev;
            }
        }

        return current;
    }
    public LinkedListTabulatedFunction() {
        // Конструктор по умолчанию для clone()
        this.head = new FunctionNode(null);
        this.head.next = this.head;
        this.head.prev = this.head;
        this.pointsCount = 0;
    }
    public LinkedListTabulatedFunction(FunctionPoint[] points) {
        if (points.length < 2) {
            throw new IllegalArgumentException("Количество точек меньше двух");
        }
        for (int i = 0; i < points.length - 1; i++) { // Проверка упорядоченности точек по X
            if (points[i].getX() >= points[i + 1].getX()) {
                throw new IllegalArgumentException("Точки не упорядочены по значению абсциссы");
            }
        }
        // Инициализация списка
        head = new FunctionNode(null);
        head.next = head;
        head.prev = head;
        this.pointsCount = 0;
        for (FunctionPoint point : points) { // Добавление точек в список
            addNodeToTail().point = new FunctionPoint(point); // Создаем копию
        }
    }
    private FunctionNode addNodeToTail() {
        FunctionNode newNode = new FunctionNode(null);
        FunctionNode lastNode = head.prev;
        // Устанавливаем связи нового узла:
        newNode.prev = lastNode;  // предыдущий для нового - старый последний
        newNode.next = head;      // следующий для нового - голова
        // Обновляем связи соседних узлов:
        lastNode.next = newNode;  // следующий для старого последнего - новый
        head.prev = newNode;      // предыдущий для головы - новый
        pointsCount++;
        return newNode;
    }
    private FunctionNode addNodeByIndex(int index) {
        // Проверка корректности индекса
        if (index < 0 || index > pointsCount) {
            throw new FunctionPointIndexOutOfBoundsException("Индекс выходит за границы: " + index);
        }
        // Если вставляем в конец, используем существующий метод
        if (index == pointsCount) {
            return addNodeToTail();
        }
        FunctionNode newNode = new FunctionNode(null);
        // Находим узел, который будет находиться после нового (по указанному индексу)
        FunctionNode nextNode = getNodeByIndex(index);
        // Узел, который будет находиться перед новым
        FunctionNode prevNode = nextNode.prev;
        // Устанавливаем связи нового узла:
        newNode.prev = prevNode;  // предыдущий для нового - узел перед nextNode
        newNode.next = nextNode;  // следующий для нового - nextNode
        // Обновляем связи соседних узлов:
        prevNode.next = newNode;  // следующий для prevNode - новый
        nextNode.prev = newNode;  // предыдущий для nextNode - новый
        pointsCount++;

        return newNode;
    }
    private FunctionNode deleteNodeByIndex(int index) {
        // Проверка корректности индекса
        if (index < 0 || index >= pointsCount) {
            throw new FunctionPointIndexOutOfBoundsException("Индекс выходит за границы: " + index);
        }
        // Находим узел для удаления
        FunctionNode nodeToDelete = getNodeByIndex(index);
        // Получаем соседние узлы
        FunctionNode prevNode = nodeToDelete.prev;
        FunctionNode nextNode = nodeToDelete.next;
        // Удаляем узел из списка - перебрасываем связи
        prevNode.next = nextNode;  // следующий для предыдущего - становится следующий от удаляемого
        nextNode.prev = prevNode;  // предыдущий для следующего - становится предыдущий от удаляемого
        // Очищаем связи удаленного узла
        nodeToDelete.prev = null;
        nodeToDelete.next = null;
        pointsCount--;
        return nodeToDelete;
    }
    public LinkedListTabulatedFunction(double leftX, double rightX, int pointsCount) {
        if (leftX >= rightX) {
            throw new IllegalArgumentException("Левая граница области определения больше или равна правой");
        }
        if (pointsCount < 2) {
            throw new IllegalArgumentException("Количество точек меньше двух");
        }
        // Инициализация списка
        head = new FunctionNode(null);
        head.next = head;
        head.prev = head;
        this.pointsCount = 0;

        double step = (rightX - leftX) / (pointsCount - 1);
        for (int i = 0; i < pointsCount; i++) {
            double x = leftX + i * step;
            addNodeToTail().point = new FunctionPoint(x, 0.0);
        }
    }

    public LinkedListTabulatedFunction(double leftX, double rightX, double[] values) {
        if (leftX >= rightX) {
            throw new IllegalArgumentException("Левая граница области определения больше или равна правой");
        }
        if (values.length < 2) {
            throw new IllegalArgumentException("Количество точек меньше двух");
        }

        // Инициализация списка
        head = new FunctionNode(null);
        head.next = head;
        head.prev = head;
        this.pointsCount = 0;

        double step = (rightX - leftX) / (values.length - 1);
        for (int i = 0; i < values.length; i++) {
            double x = leftX + i * step;
            addNodeToTail().point = new FunctionPoint(x, values[i]);
        }
    }
    public double getLeftDomainBorder() {
        if (pointsCount == 0) return Double.NaN;
        return head.next.point.getX(); // Первый значащий узел
    }

    public double getRightDomainBorder() {
        if (pointsCount == 0) return Double.NaN;
        return head.prev.point.getX(); // Последний значащий узел
    }

    public double getFunctionValue(double x) {
        if (pointsCount == 0 || x < getLeftDomainBorder() || x > getRightDomainBorder()) {
            return Double.NaN;
        }
        // Используем прямое обращение к узлам для поиска интервала
        FunctionNode current = head.next;
        while (current != head) {
            FunctionNode next = current.next;
            if (next != head && x >= current.point.getX() && x <= next.point.getX()) {
                // Линейная интерполяция
                double x1 = current.point.getX();
                double x2 = next.point.getX();
                double y1 = current.point.getY();
                double y2 = next.point.getY();
                return y1 + (y2 - y1) * (x - x1) / (x2 - x1);
            }
            current = current.next;
        }

        return Double.NaN;
    }

    public int getPointsCount() {
        return pointsCount;
    }

    public FunctionPoint getPoint(int index) {
        FunctionNode node = getNodeByIndex(index);
        return new FunctionPoint(node.point); // Возвращаем копию
    }

    public void setPoint(int index, FunctionPoint point) throws InappropriateFunctionPointException {
        FunctionNode node = getNodeByIndex(index);
        double newX = point.getX();

        // Проверка порядка точек
        if (pointsCount == 1) {
            node.point = new FunctionPoint(point);
            return;
        }

        if (index == 0) {
            // Первый элемент: новая X должна быть < следующей
            if (newX >= node.next.point.getX()) {
                throw new InappropriateFunctionPointException("Новая X для первой точки должна быть меньше следующей точки");
            }
        } else if (index == pointsCount - 1) {
            // Последний элемент: новая X должна быть > предыдущей
            if (newX <= node.prev.point.getX()) {
                throw new InappropriateFunctionPointException("Новая X для последней точки должна быть больше предыдущей точки");
            }
        } else {
            // Средний элемент: новая X должна быть между соседними
            if (newX <= node.prev.point.getX() || newX >= node.next.point.getX()) {
                throw new InappropriateFunctionPointException("Новая X должна быть между соседними точками");
            }
        }

        node.point = new FunctionPoint(point);
    }

    public double getPointX(int index) {
        return getNodeByIndex(index).point.getX();
    }

    public void setPointX(int index, double x) throws InappropriateFunctionPointException {
        FunctionNode node = getNodeByIndex(index);
        double newX = x;

        // Проверка порядка точек
        if (pointsCount == 1) {
            node.point.setX(x);
            return;
        }

        if (index == 0) {
            if (newX >= node.next.point.getX()) {
                throw new InappropriateFunctionPointException("Новая X для первой точки должна быть меньше следующей точки");
            }
        } else if (index == pointsCount - 1) {
            if (newX <= node.prev.point.getX()) {
                throw new InappropriateFunctionPointException("Новая X для последней точки должна быть больше предыдущей точки");
            }
        } else {
            if (newX <= node.prev.point.getX() || newX >= node.next.point.getX()) {
                throw new InappropriateFunctionPointException("Новая X должна быть между соседними точками");
            }
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
        if (pointsCount < 3) {
            throw new IllegalStateException("Невозможно удалить точку: количество точек меньше трех");
        }
        deleteNodeByIndex(index);
    }

    public void addPoint(FunctionPoint point) throws InappropriateFunctionPointException {
        // Проверка на существование точки с таким X
        FunctionNode current = head.next;
        while (current != head) {
            if (Math.abs(current.point.getX() - point.getX()) < 1e-10) {
                throw new InappropriateFunctionPointException("Точка с такой X уже существует");
            }
            current = current.next;
        }

        // Поиск позиции для вставки
        int insertIndex = 0;
        current = head.next;
        while (current != head && current.point.getX() < point.getX()) {
            current = current.next;
            insertIndex++;
        }
        // Используем готовый метод для вставки
        FunctionNode newNode = addNodeByIndex(insertIndex);
        newNode.point = new FunctionPoint(point);
    }
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("{");
        FunctionNode current = head.next;
        int count = 0;
        while (current != head) {
            sb.append("(").append(current.point.getX())
                    .append("; ").append(current.point.getY()).append(")");
            if (count < pointsCount - 1) {
                sb.append(", ");
            }
            current = current.next;
            count++;
        }
        sb.append("}");
        return sb.toString();
    }
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof TabulatedFunction)) return false;

        TabulatedFunction other = (TabulatedFunction) o;

        // Проверка количества точек
        if (this.getPointsCount() != other.getPointsCount()) return false;

        // Оптимизация для LinkedListTabulatedFunction
        if (o instanceof LinkedListTabulatedFunction) {
            LinkedListTabulatedFunction otherList = (LinkedListTabulatedFunction) o;

            // Прямое сравнение узлов двух списков
            FunctionNode currentThis = this.head.next;
            FunctionNode currentOther = otherList.head.next;

            while (currentThis != head && currentOther != otherList.head) {
                if (!currentThis.point.equals(currentOther.point)) {
                    return false;
                }
                currentThis = currentThis.next;
                currentOther = currentOther.next;
            }
            return true;
        }

        // Общий случай для любой TabulatedFunction
        for (int i = 0; i < this.getPointsCount(); i++) {
            if (!this.getPoint(i).equals(other.getPoint(i))) {
                return false;
            }
        }
        return true;
    }
    @Override
    public int hashCode() {
        int hash = pointsCount; // Начало с количества точек

        // Комбинирование хэш-кодов всех точек через XOR
        FunctionNode current = head.next;
        while (current != head) {
            hash ^= current.point.hashCode();
            current = current.next;
        }

        return hash;
    }
    @Override
    public Object clone() {
        // Временный объект
        LinkedListTabulatedFunction cloned = new LinkedListTabulatedFunction();

        // Пустой список
        cloned.head = new FunctionNode(null);
        cloned.head.next = cloned.head;
        cloned.head.prev = cloned.head;
        cloned.pointsCount = 0;

        // Если исходный список пуст, возврат пустого клона
        if (this.pointsCount == 0) {
            return cloned;
        }

        // Пересборка списка вручную без методов добавления
        FunctionNode currentOriginal = this.head.next;
        FunctionNode lastClonedNode = cloned.head;

        while (currentOriginal != this.head) {
            // Новая точка
            FunctionPoint newPoint = new FunctionPoint(
                    currentOriginal.point.getX(),
                    currentOriginal.point.getY()
            );

            // Новый узел с новой точкой
            FunctionNode newNode = new FunctionNode(newPoint);

            // Прямое связывание узлов без методов добавления
            newNode.prev = lastClonedNode;
            newNode.next = cloned.head;
            lastClonedNode.next = newNode;
            cloned.head.prev = newNode;

            // Переход к следующему узлу
            lastClonedNode = newNode;
            currentOriginal = currentOriginal.next;
            cloned.pointsCount++;
        }

        return cloned;
    }
    @Override
    public Iterator<FunctionPoint> iterator() {
        return new Iterator<FunctionPoint>() {
            private FunctionNode currentNode = head.next;
            private int currentIndex = 0;

            @Override
            public boolean hasNext() {
                return currentNode != head && currentIndex < pointsCount;
            }

            @Override
            public FunctionPoint next() {
                if (!hasNext()) {
                    throw new java.util.NoSuchElementException("No more elements");
                }
                FunctionPoint point = new FunctionPoint(currentNode.point);
                currentNode = currentNode.next;
                currentIndex++;
                return point;
            }

            @Override
            public void remove() {
                throw new UnsupportedOperationException("Remove operation is not supported");
            }
        };
    }

    public void printTabulatedFunction() {
        FunctionNode current = head.next;
        int index = 0;
        while (current != head) {
            System.out.println(index + ": x = " + current.point.getX() + ", y = " + current.point.getY());
            current = current.next;
            index++;
        }
    }
    public static class LinkedListTabulatedFunctionFactory implements TabulatedFunctionFactory {
        @Override
        public TabulatedFunction createTabulatedFunction(double leftX, double rightX, int pointsCount) {
            return new LinkedListTabulatedFunction(leftX, rightX, pointsCount);
        }

        @Override
        public TabulatedFunction createTabulatedFunction(double leftX, double rightX, double[] values) {
            return new LinkedListTabulatedFunction(leftX, rightX, values);
        }

        @Override
        public TabulatedFunction createTabulatedFunction(FunctionPoint[] points) {
            return new LinkedListTabulatedFunction(points);
        }
    }
}