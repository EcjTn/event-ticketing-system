ALTER TABLE order_item
ADD CONSTRAINT uq_order_item_order_tier UNIQUE (order_id, event_tier_id);