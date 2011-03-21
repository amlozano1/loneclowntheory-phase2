SELECT A.entityName as subj, B.entityName as obj, A.curr_sensitivity as subjLvl, B.curr_sensitivity as objLvl, 
LPAD(BIN(A.curr_category+0),11,'0') as subjcat, LPAD(BIN(B.curr_category+0),11,'0') as objcat, LPAD(BIN(A.curr_category+0 & B.curr_category+0),11,'0') as result 
FROM loneclowntheory.entityTable AS A, loneclowntheory.entityTable AS B
HAVING subj='subject0' AND obj='subject0' AND objcat=result AND objLvl<=subjLvl;