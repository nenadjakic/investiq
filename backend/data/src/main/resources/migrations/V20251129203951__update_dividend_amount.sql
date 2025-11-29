update transactions
set amount = gross_amount - tax_amount
where transaction_type = 'DIVIDEND'
	and amount is null