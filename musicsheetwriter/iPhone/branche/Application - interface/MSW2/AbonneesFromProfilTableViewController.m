//
//  AbonneesFromProfilTableViewController.m
//  MSW
//
//  Created by simon on 15/12/2015.
//  Copyright (c) 2015 Score Lab. All rights reserved.
//

#import "AbonneesFromProfilTableViewController.h"
#import "SubscriptionCell.h"
#import "ApiMethod.h"
#import "ProfilViewController.h"

@interface AbonneesFromProfilTableViewController ()

@end

@implementation AbonneesFromProfilTableViewController

- (void)viewDidLoad {
    [super viewDidLoad];
    [self refreshView];
}

- (void)didReceiveMemoryWarning {
    [super didReceiveMemoryWarning];
    // Dispose of any resources that can be recreated.
}

-(void) refreshView{
    NSString * post =[NSString stringWithFormat:@"https://musicsheetwriter.tk/api/users/%@/subscribers", idProfil_global];
    ApiMethod *api = [[ApiMethod alloc]init];
    NSDictionary *dict1 = [api ApiMethodGetAfnetworkingAt:post];
    
    if (code_global != 200){
        [api popup:dict1 id:self];
        return;
    }
    _pseudo = [[NSMutableArray alloc] initWithCapacity:0];
    _idRow = [[NSMutableArray alloc] initWithCapacity:0];
    for(NSDictionary *item in dict1) {
        [_pseudo addObject:[item valueForKey:@"username"]];
        [_idRow addObject:[item valueForKey:@"id"]];
    }
}

- (NSInteger)numberOfSectionsInTableView:(UITableView *)tableView {
    // Return the number of sections.
    return 1;
}

- (NSInteger)tableView:(UITableView *)tableView numberOfRowsInSection:(NSInteger)section {
    // Return the number of rows in the section.
    return _pseudo.count;
}

- (UITableViewCell *)tableView:(UITableView *)tableView cellForRowAtIndexPath:(NSIndexPath *)indexPath {
    
    static NSString *cellId = @"SubscriptionCell";
    SubscriptionCell *cell = [tableView dequeueReusableCellWithIdentifier:cellId forIndexPath:indexPath];
    cell.pseudo.text = _pseudo[indexPath.row];
    return cell;
}

-(void) prepareForSegue:(UIStoryboardSegue *)segue sender:(id)sender{
    if ([[segue identifier] isEqualToString:@"ShowProfil"]){
        ProfilViewController *profilController = [segue destinationViewController];
        NSIndexPath *myIndexPath = [self.tableView indexPathForSelectedRow];
        profilController.ProfilModal = @[_pseudo[myIndexPath.row], _idRow[myIndexPath.row]];
    }
}

@end
