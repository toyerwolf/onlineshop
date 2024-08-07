WITH deleted_user AS (
DELETE FROM users_test WHERE username = 'test_user' RETURNING id
)
DELETE FROM customer WHERE user_id = (SELECT id FROM deleted_user)