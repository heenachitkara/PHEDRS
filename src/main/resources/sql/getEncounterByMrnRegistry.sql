SELECT re.formatted_financial_nbr, admission_source, admit_loc, re.encounter_key, encounter_type_class_ref, 
end_date, inout_cd, reason_for_visit_txt, start_date, 
cvr.desc_meaning as encounter_type_description, 
locs.ambdisp as loc_ambulatory_desc
, billplan 
FROM registry_patient rp 
JOIN registry_encounter re on re.formatted_medical_record_nbr = rp.formatted_uab_mrn 
JOIN (
SELECT enctype.name as desc_meaning, enctyperef.accession as code_value_ref
FROM cvterm enctype JOIN dbxref enctyperef ON (enctype.dbxref_id=enctyperef.dbxref_id)
WHERE enctype.cv_id=27
) cvr ON (cvr.code_value_ref=re.ENCOUNTER_TYPE_CLASS_REF)
JOIN (
SELECT locname.definition as ambdisp,locref.accession location_sk
FROM cvterm locname JOIN dbxref locref ON (locname.dbxref_id=locref.dbxref_id)
WHERE locname.cv_id=28
) locs ON (locs.location_sk = admit_loc)
JOIN ( 
  SELECT HQFINNUM as fin , MIN(rbc.plnfscrptccat) as billplan
  FROM registry_billed_charges rbc GROUP BY HQFINNUM ) mso ON (re.formatted_financial_nbr=mso.fin) 
WHERE rp.registry_id = ? AND rp.uab_mrn = ? 

