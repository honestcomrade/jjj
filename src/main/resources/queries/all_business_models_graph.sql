SELECT *
FROM public.business_models
JOIN public.parents ON business_models.parent_id = parents.id
JOIN public.child1 ON business_models.child1_id = child1.id
JOIN public.child2 ON business_models.child2_id = child2.id
JOIN public.grandchild1 ON business_models.grandchild1_id = grandchild1.id
LIMIT 1000;