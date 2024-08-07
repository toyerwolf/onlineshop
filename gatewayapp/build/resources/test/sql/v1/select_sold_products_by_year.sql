SELECT op.product_id, SUM(op.quantity) AS total_sold, p.name AS product_name, YEAR(o.created_at) AS year
FROM order_product op
    JOIN order_test o ON op.order_id = o.id
    JOIN product_test p ON op.product_id = p.id
WHERE YEAR(o.created_at) = :year
  AND o.status = 'PAID'
GROUP BY p.name, op.product_id, YEAR(o.created_at);