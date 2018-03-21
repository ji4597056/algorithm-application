package com.github.ji4597056;

import java.util.Comparator;
import java.util.Objects;

/**
 * red-black tree
 *
 * @author Jeffrey
 * @since 2018/03/19 13:14
 */
public class RedBlackTree<T> {

    /**
     * red node flag
     */
    private static final boolean RED = false;

    /**
     * black node flag
     */
    private static final boolean BLACK = true;

    /**
     * root node
     */
    private Node<T> root;

    /**
     * comparator
     */
    private final Comparator<? super T> comparator;

    /**
     * tree size
     */
    private int size;

    public RedBlackTree() {
        this.comparator = Comparator.comparingInt(Object::hashCode);
    }

    public RedBlackTree(Comparator<? super T> comparator) {
        this.comparator = comparator;
    }

    /**
     * remove by value
     */
    public boolean remove(T value) {
        Node<T> p = find(value);
        if (p == null) {
            return false;
        }
        deleteNode(p);
        return true;
    }

    /**
     * delete node
     */
    private void deleteNode(Node<T> p) {
        size--;
        // 若待当前节点(p)左孩子和右孩子都存在,则找到该节点(p)的中序遍历的后继节点,交换值,并将当前节点(p)指向后继节点
        // 后继节点为该节点右子树的最小左节点
        // 当当前节点(p)指向后继节点时,只会出现两种情况:1.仅有右子结点 2.无子节点
        if (p.left != null && p.right != null) {
            Node<T> s = successor(p);
            p.value = s.value;
            p = s;
        }
        // 设置替换节点,此时只会出现三种情况: 1.仅有右子节点 2.仅有左子节点 3.无子节点
        Node<T> replacement = (p.left != null ? p.left : p.right);
        // 1.当有子节点时(仅有右子节点或仅有左子节点)进行修复操作
        if (replacement != null) {
            // 将替换节点与当前节点(p)的父节点链接
            replacement.parent = p.parent;
            // 若当前节点(p)为root,则将root指向修复节点
            if (p.parent == null) {
                root = replacement;
            } else if (p == p.parent.left) {
                p.parent.left = replacement;
            } else {
                p.parent.right = replacement;
            }
            // 将当前节点(p)与树解除链接(会被gc释放内存)
            p.left = p.right = p.parent = null;
            // 若当前节点(p)为黑色,删除后会破坏红黑树的性质,因此需要进行修复操作
            // 修复操作这里传的的是替换节点,后文中传的是当前节点(p),因为:
            // (1)为了获取父节点以及兄弟节点引用
            // (2)替换节点为红色则直接置黑色,即恢复红黑树性质,替换节点为null(对应后文中传p)或黑色,则从兄弟节点去借调节点进行修复
            if (p.color == BLACK) {
                fixAfterDeletion(replacement);
            }
            // 2.当无子节点时进行修复操作
        } else if (p.parent == null) {
            root = null;
        } else {
            // 若当前节点(p)为黑色,删除后会破坏红黑树的性质,因此需要进行修复操作
            if (p.color == BLACK) {
                fixAfterDeletion(p);
            }
            // 将当前节点(p)与树解除链接(会被gc释放内存)
            if (p.parent != null) {
                if (p == p.parent.left) {
                    p.parent.left = null;
                } else if (p == p.parent.right) {
                    p.parent.right = null;
                }
                p.parent = null;
            }
        }
    }

    /**
     * fix tree after deleting node
     */
    private void fixAfterDeletion(Node<T> p) {
        // 替换节点为红色,则直接置黑色即修复完成
        // 替换节点为null/黑,则视情况讨论,总体思路为如何从兄弟节点去借调节点
        while (p != root && !colorEqual(p, RED)) {
            // 1.当前节点(p)为左节点
            if (p == leftOf(parentOf(p))) {
                // 获取兄弟节点(sib)
                Node<T> sib = rightOf(parentOf(p));
                // 1.1 兄弟节点(sib)为红色(中间状态)
                if (colorEqual(sib, RED)) {
                    setColor(sib, BLACK);
                    setColor(parentOf(p), RED);
                    rotateLeft(parentOf(p));
                    // 获取新的兄弟节点(sib),此时兄弟节点(sib)为黑色
                    sib = rightOf(parentOf(p));
                }
                // 1.2 兄弟节点(sib)为黑时,左右侄子节点都不为红色(中间状态)
                if (!colorEqual(leftOf(sib), RED) && !colorEqual(rightOf(sib), RED)) {
                    setColor(sib, RED);
                    // 父节点(可红可黑)的左右子节点的黑节点数量均少1,此时需要从父节点开始继续调整
                    p = parentOf(p);
                } else {
                    // 1.3 兄弟节点(sib)为黑时,左侄子为红色,右侄子为null/黑(中间状态)
                    if (!colorEqual(rightOf(sib), RED)) {
                        setColor(leftOf(sib), BLACK);
                        setColor(sib, RED);
                        rotateRight(sib);
                        sib = rightOf(parentOf(p));
                    }
                    // 1.4 兄弟节点(sib)为黑时,右侄子为红色,左侄子为null/黑/红
                    setColor(sib, colorOf(parentOf(p)));
                    setColor(parentOf(p), BLACK);
                    setColor(rightOf(sib), BLACK);
                    rotateLeft(parentOf(p));
                    // 无需继续修复,跳出循环
                    p = root;
                }
                // 2.当前节点(p)为右节点
            } else {
                // 获取兄弟节点(sib)
                Node<T> sib = leftOf(parentOf(p));
                // 2.1 兄弟节点(sib)为红色
                if (colorOf(sib) == RED) {
                    setColor(sib, BLACK);
                    setColor(parentOf(p), RED);
                    rotateRight(parentOf(p));
                    // 获取新的兄弟节点(sib),此时兄弟节点(sib)为黑色
                    sib = leftOf(parentOf(p));
                }
                // 2.2 兄弟节点(sib)为黑时,左右侄子节点都不为红色
                if (!colorEqual(rightOf(sib), RED) && !colorEqual(leftOf(sib), RED)) {
                    setColor(sib, RED);
                    // 父节点(可红可黑)的左右子节点的黑节点数量均少1,此时需要从父节点开始继续调整
                    p = parentOf(p);
                } else {
                    // 2.3 兄弟节点(sib)为黑时,左侄子为红色,右侄子为null/黑
                    if (!colorEqual(leftOf(sib), RED)) {
                        setColor(rightOf(sib), BLACK);
                        setColor(sib, RED);
                        rotateLeft(sib);
                        sib = leftOf(parentOf(p));
                    }
                    // 2.4 兄弟节点(sib)为黑时,右侄子为红色,左侄子为null/黑/红
                    setColor(sib, colorOf(parentOf(p)));
                    setColor(parentOf(p), BLACK);
                    setColor(leftOf(sib), BLACK);
                    rotateRight(parentOf(p));
                    // 无需继续修复,跳出循环
                    p = root;
                }
            }
        }
        setColor(p, BLACK);
    }

    /**
     * get successor node of inorder traversal
     */
    private static <T> Node<T> successor(Node<T> p) {
        if (p == null) {
            return null;
        } else if (p.right != null) {
            Node<T> n = p.right;
            while (n.left != null) {
                n = n.left;
            }
            return n;
        } else {
            Node<T> n = p.parent;
            Node<T> ch = p;
            while (n != null && ch == n.right) {
                ch = n;
                n = n.parent;
            }
            return n;
        }
    }

    /**
     * add node
     */
    public void add(T value) {
        Node<T> t = root;
        // 添加root node
        if (t == null) {
            root = new Node<>(value, null);
            size++;
            return;
        }
        // 遍历添加node为叶子节点
        int cmp;
        Node<T> parent;
        do {
            parent = t;
            cmp = comparator.compare(value, t.value);
            if (cmp < 0) {
                t = t.left;
            } else if (cmp > 0) {
                t = t.right;
            } else {
                // 如果存在该node直接返回
                return;
            }
        } while (t != null);
        Node<T> p = new Node<>(value, parent);
        if (cmp < 0) {
            parent.left = p;
        } else {
            parent.right = p;
        }
        // 添加后修复树
        fixAfterAdd(p);
        size++;
    }

    /**
     * fix tree after adding node
     */
    private void fixAfterAdd(Node<T> p) {
        // 新插入的节点为红色的
        p.color = RED;
        // 如果遇到父节点的颜色为黑则进行修复操作结束,因为此时添加红节点并不会破坏树的平衡性
        // 也就是说,只有在父节点为红色节点的时候是需要插入修复操作的
        while (p != null && p != root && p.parent.color == RED) {
            // 1.父节点(红色)属于左节点
            if (parentOf(p) == leftOf(parentOf(parentOf(p)))) {
                // q为叔叔节点(父节点的右兄弟节点)
                Node<T> q = rightOf(parentOf(parentOf(p)));
                // 1.1 叔叔节点为红
                if (colorEqual(q, RED)) {
                    setColor(parentOf(p), BLACK);
                    setColor(q, BLACK);
                    setColor(parentOf(parentOf(p)), RED);
                    // 爷爷节点由黑变红,可能需要从爷爷节点开始回溯修复
                    // 因为父节点红,爷爷节点必黑,曾祖父节点可红可黑,当爷爷节点变红,此时与曾祖父节点为红时冲突,需要修复
                    p = parentOf(parentOf(p));
                } else {
                    // 1.2 叔叔节点为null/黑
                    // 1.2.1 当前节点属于右节点(中间状态),需要左旋父节点,使得当前节点变为左节点
                    if (p == rightOf(parentOf(p))) {
                        p = parentOf(p);
                        rotateLeft(p);
                    }
                    // 1.2.2 当前节点属于左节点,右旋爷爷节点,使之平衡
                    setColor(parentOf(p), BLACK);
                    setColor(parentOf(parentOf(p)), RED);
                    rotateRight(parentOf(parentOf(p)));
                }
                // 2.父节点(红色)属于右节点
            } else {
                // q为叔叔节点(父节点的左兄弟节点)
                Node<T> q = leftOf(parentOf(parentOf(p)));
                // 2.1 叔叔节点为红
                if (colorEqual(q, RED)) {
                    setColor(parentOf(p), BLACK);
                    setColor(q, BLACK);
                    setColor(parentOf(parentOf(p)), RED);
                    // 爷爷节点由黑变红,可能需要从爷爷节点开始回溯修复
                    p = parentOf(parentOf(p));
                } else {
                    // 2.2 叔叔节点为null/黑
                    // 2.2.1 当前节点属于左节点(中间状态),需要右旋父节点,使得当前节点变为右节点
                    if (p == leftOf(parentOf(p))) {
                        p = parentOf(p);
                        rotateRight(p);
                    }
                    // 2.2.2 当前节点属于右节点,左旋爷爷节点,使之平衡
                    setColor(parentOf(p), BLACK);
                    setColor(parentOf(parentOf(p)), RED);
                    rotateLeft(parentOf(parentOf(p)));
                }
            }
        }
        // 根节点永远为黑
        root.color = BLACK;
    }

    /**
     * get parent of node
     */
    private static <T> Node<T> parentOf(Node<T> p) {
        return (p == null ? null : p.parent);
    }

    /**
     * get left of node
     */
    private static <T> Node<T> leftOf(Node<T> p) {
        return (p == null) ? null : p.left;
    }

    /**
     * get right of node
     */
    private static <T> Node<T> rightOf(Node<T> p) {
        return (p == null) ? null : p.right;
    }

    /**
     * get color of node,return BLACK if node is null
     */
    private static <T> boolean colorOf(Node<T> p) {
        return (p == null ? BLACK : p.color);
    }

    /**
     * check color of node equal
     */
    private static <T> boolean colorEqual(Node<T> p, boolean c) {
        return p != null && p.color == c;
    }

    /**
     * set color of node
     */
    private static <T> void setColor(Node<T> p, boolean c) {
        if (p != null) {
            p.color = c;
        }
    }

    /**
     * find node by value
     */
    public Node<T> find(T value) {
        Comparator<? super T> cpr = comparator;
        if (cpr != null) {
            Node<T> p = root;
            while (p != null) {
                int cmp = cpr.compare(value, p.value);
                if (cmp < 0) {
                    p = p.left;
                } else if (cmp > 0) {
                    p = p.right;
                } else {
                    return p;
                }
            }
        }
        return null;
    }

    /**
     * rotate left
     */
    private void rotateLeft(Node<T> p) {
        if (p != null) {
            Node<T> right = p.right;
            p.right = right.left;
            if (right.left != null) {
                right.left.parent = p;
            }
            right.parent = p.parent;
            if (p.parent == null) {
                root = right;
            } else if (p.parent.left == p) {
                p.parent.left = right;
            } else {
                p.parent.right = right;
            }
            // P变为左节点
            right.left = p;
            p.parent = right;
        }
    }

    /**
     * rotate right
     */
    private void rotateRight(Node<T> p) {
        if (p != null) {
            Node<T> left = p.left;
            p.left = left.right;
            if (left.right != null) {
                left.right.parent = p;
            }
            left.parent = p.parent;
            if (p.parent == null) {
                root = left;
            } else if (p.parent.right == p) {
                p.parent.right = left;
            } else {
                p.parent.left = left;
            }
            // P变为右节点
            left.right = p;
            p.parent = left;
        }
    }

    /**
     * tree node
     */
    public static final class Node<T> {
        T value;
        Node<T> left;
        Node<T> right;
        Node<T> parent;
        boolean color = BLACK;

        Node(T value, Node<T> parent) {
            this.value = value;
            this.parent = parent;
        }

        public T getValue() {
            return value;
        }

        public T setValue(T value) {
            T oldValue = this.value;
            this.value = value;
            return oldValue;
        }

        @Override
        public boolean equals(Object o) {
            if (!(o instanceof Node)) {
                return false;
            }
            Node<?> e = (Node<?>) o;
            return Objects.equals(value, e.getValue());
        }

        @Override
        public int hashCode() {
            return (value == null ? 0 : value.hashCode());
        }

        @Override
        public String toString() {
            return value.toString();
        }
    }
}
