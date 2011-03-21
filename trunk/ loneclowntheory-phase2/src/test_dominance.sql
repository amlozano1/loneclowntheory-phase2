SELECT A.entityName as subj, B.entityName as obj, A.sensitivity as subjLvl, B.sensitivity as objLvl, 
LPAD(BIN(A.category+0),11,'0') as subjcat, LPAD(BIN(B.category+0),11,'0') as objcat, LPAD(BIN(A.category+0 & B.category+0),11,'0') as result 
FROM loneclowntheory.entityTable AS A, loneclowntheory.entityTable AS B
HAVING subj='s1' AND obj='o1' AND objcat=result AND objLvl<=subjLvl;